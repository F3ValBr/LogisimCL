module factorial(
  input clk, start,
  input [7:0] n,
  output [31:0] f,
  output ready );

reg [7:0] RN;
reg [31:0] RF;

wire mul_rdy, store, mul_start;
assign ready = RN < 8'd1;

serial_multiplier mul(clk, mul_start, (32)'(RN), RF, f, mul_rdy);

ctrl ctrl(clk, start, ready, mul_rdy, store, mul_start);

always_ff @(negedge clk) begin
  if (store) begin
    RN <= start ? n : RN - 8'd1;
    RF <= start ? 32'd1 : f;
  end
end

endmodule
