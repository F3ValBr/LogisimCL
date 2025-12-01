module serial_multiplier(
  input clk, start,
  input [31:0] x, y,
  output [31:0] z,
  output ready );

reg [31:0] RX, RY, RZ;

assign ready = RY == 32'd0;
assign z = RZ;

always_ff @(negedge clk) begin
  RX <= start ? x : (RX<<32'd1);
  RY <= start ? y : (RY>>32'd1);
  RZ <= start ? 32'd0 : (RZ + (RY[0] ? RX : 32'd0));
end

endmodule
