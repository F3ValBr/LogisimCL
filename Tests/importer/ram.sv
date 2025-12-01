module ram(
  input clk, str,
  input [3:0] addr,
  input [7:0] x,
  output logic [7:0] data );

reg [7:0] mem[15:0];

initial begin
  for (int i= 0; i<16; i++)
    mem[i]= 0;
end

always_comb
  data = mem[addr];

always @(negedge clk)
  if (str) begin
    mem[addr] <= x;
    $display("store");
  end

endmodule
