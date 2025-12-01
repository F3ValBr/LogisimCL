module counter( input clk, reset, enable,
                output reg [3:0] count );
  always @(negedge clk)
    if (reset)
      count <= 0;
    else if (enable)
      count <= count + 4'd1; // count <= count + 4'd1
endmodule
