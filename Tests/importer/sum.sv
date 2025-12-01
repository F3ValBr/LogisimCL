// Perform combinatorial addition of 32 bits by using 4 5-bit adders
module sum(
  input [31:0] x, y,
  output logic carry,
  output reg [31:0] z);

logic [7:0] res;

always_comb begin
  carry = 0;
  for (int k= 0; k<32; k = k + 8) begin
    // $display("k=%d", k);
    {carry, res} = {1'b0, x[k +: 8]} + {1'b0, y[k +: 8]} + (9)'(carry);
    z[k +: 8] = res;
  end
end

endmodule
