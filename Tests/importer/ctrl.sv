module ctrl(
  input clk, start, ready, mul_rdy,
  output logic store, mul_start );

enum logic [2:0] { st_idle=1, st_start=2, st_mul=4 } state, next;

initial state = st_idle;

always_ff @(negedge clk)
  state <= next;

always_comb begin
  store = 0;
  mul_start = 0;
  if (start) begin
    store = 1;
    next = st_start;
  end
  else if (ready)
    next = st_idle;
  else if (state == st_start) begin
    mul_start = 1;
    next = st_mul;
  end
  else if (state == st_mul && mul_rdy) begin
    store = 1;
    next = st_start;
  end
  else
    next = state;
end
    
endmodule
