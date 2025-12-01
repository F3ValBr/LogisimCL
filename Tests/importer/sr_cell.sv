module gen_sr (
  input  wire S,      // Set
  input  wire R,      // Reset
  output reg  Q
);
  always @* begin
    if (S)
      Q = 1'b1;       // Set
    else if (R)
      Q = 1'b0;       // Reset
    // si S=R=0 mantiene el estado anterior
  end
endmodule
