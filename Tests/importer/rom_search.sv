module rom_search (
  input clk, start,
  input reset,
  input [7:0] x,
  output rdy, found );


reg [3:0] regCnt /* = 0 */;
reg [7:0] regX /* = 0 */;
wire [7:0] data;

rom rom({4'd0,regCnt}, data);

wire [3:0] incCnt;
wire carry;

assign {carry, incCnt}= {1'b0, regCnt} + 5'd1;
assign found = data == regX;
assign rdy = found || carry;

/* verilator lint_off SYNCASYNCNET */
always @(negedge clk or posedge reset) begin
  if (reset) begin
    regCnt <= 0;
    regX <= 0;
    // $display("reset");
  end
  else
  if (start || !rdy) begin
    regCnt <= start ? 4'd0 : incCnt;
    regX <= start ? x : regX;
  end
end
/* verilator lint_on SYNCASYNCNET */

endmodule
