module p1( 
  input clk, start,
  input [31:0] x,
  output rdy,
  output reg [5:0] z );

reg [31:0] XReg; // tambien puede ser logic

assign rdy = XReg==0; // Calculo de rdy: 1 punto

always_ff @(negedge clk) begin // Uso de always o always_ff: 1 punto
  if (start)  // Calculo de XReg caso start: 0,5 punto
    XReg <= x;
  else // Calculo de XReg, caso !start: 1 punto
    XReg <= (XReg<<1) & XReg;

  if (start || !rdy) begin  // Modifica z solo si start || !rdy: 1 punto
    if (start) // Calculo de z, caso start: 0,5 puntos
      z <= 6'd0;
    else // Calculo de z, caso !start: 1 punto
      z <= z + 6'd1;
  end
end

endmodule
