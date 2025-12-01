module rom(
  input [7:0] addr,
  output logic [7:0] data );

always @(addr)
  case(addr)
    0 : data = 2;
    1 : data = 3;
    2 : data = 5;
    3 : data = 7;
    4 : data = 11;
    5 : data = 13;
    default: data = 0;
  endcase
endmodule
