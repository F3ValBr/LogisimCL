module p2( input clk, start,
           input [31:0] x,
           output rdy, isp );

reg [4:0] i, j;

assign isp = x[i +: 5'd4] == x[j +: 5'd4]; // bit slice
assign rdy = i <= j || !isp;
always_ff @(negedge clk)
  if (start) begin
    i <= 5'd28;
    j <= 5'd0;
  end
  else if (!rdy) begin
    i <= i - 5'd4;
    j <= j + 5'd4;
  end

endmodule