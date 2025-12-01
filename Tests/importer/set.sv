module set(
  input  logic        clk, reset,
  input  logic        find, add,
  input  logic [7:0]  x,
  output logic        rdy,
  output logic        found
);

  // tabla 16 entradas, dato 8 bits
  logic [7:0]  ram   [0:15];
  logic [15:0] valid;
  logic [3:0]  idx;
  logic        finding, adding;

  // hash y selección de slot
  logic [3:0] hash;
  assign hash = x[3:0];

  logic [3:0] slot;
  assign slot = (add || find) ? hash : idx;

  // estado del slot y comparaciones
  logic valid_slot;
  assign valid_slot = valid[slot];

  logic find_success;
  assign find_success = (find || finding) && valid_slot && (ram[slot] == x);

  logic add_success;
  assign add_success  = (add  || adding) && !valid_slot;

  // listo cuando no estamos ni buscando ni insertando
  assign rdy = !(finding || adding);

  // detención: hit, insert hecho, regresó al hash sin start (ciclo cerrado), o slot vacío
  logic stop;
  assign stop = find_success || add_success || ((idx == hash) && ! (find || add)) || !valid_slot;

  // secuencial (negedge como pediste)
  always_ff @(negedge clk) begin
    if (reset) begin
      valid   <= '0;      // tabla vacía
      finding <= 1'b0;
      adding  <= 1'b0;
      found   <= 1'b0;
      idx     <= 4'd0;
    end else if ( (find || add) || !rdy ) begin
      // escritura en éxito de inserción
      if (add_success) begin
        ram[slot]   <= x;
        valid[slot] <= 1'b1;
      end

      // control de modo
      if (stop) begin
        finding <= 1'b0;
        adding  <= 1'b0;
      end else if (find || add) begin
        finding <= find;
        adding  <= add;
      end

      // bandera de resultado del ciclo actual
      found <= find_success | add_success;

      // avanzar índice (linear probing)
      idx   <= slot + 4'd1;  // wrap implícito en 4 bits
    end
  end

endmodule
