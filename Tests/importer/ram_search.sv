module ram_search (
  input clk, add, start,
  input reset,
  input [7:0] x,
  output rdy, found );

reg [3:0] regCnt /* = 0 */;
reg [3:0] regNxt /* = 0 */;
reg [7:0] regX /* = 0 */;
wire [7:0] data;

assign found = data == regX;
assign rdy = found | regCnt == regNxt;

wire [3:0] address = add ? regNxt : regCnt;

ram ram(clk, add, address, x, data);

always_ff @(negedge clk) begin
  if (reset) begin
    regCnt <= 0;
    regX <= 0;
    regNxt <= 0;
  end
  else begin
    if (start | !rdy) begin
      regCnt <= start ? 4'd0 : regCnt+1;
      regX <= start ? x : regX;
    end
    if (add)
      regNxt <= regNxt + 4'd1;
  end
end

endmodule
