module primegen (
  input clk,
  input start,         // 1 means start computing prime numbers up to num
  input [N-1:0] k,     // If k is prime number, p is 1
  input [N-1:0] num,   // max prime number to consider
  output reg [N:0] regI,     // Internal register
  output reg [N:0] regJ = 0, // Internal register
  output rdy,          // After the falling edge of start, prime numbers
                       // computing have finished when rdy is 1
  output p);           // 1 means that k is a prime number

parameter N = 8;


assign rdy = regI > (N+1)'(regNum);
assign p = ~not_prime;

// Generator of multiples of num

reg [N-1:0] regNum;

/* verilator lint_off UNOPTFLAT */
logic mg_start;
/* verilator lint_on UNOPTFLAT */

wire mg_rdy = regJ > (N+1)'(regNum);
logic str_i;
logic str_mem;
wire not_prime;

always_ff @(negedge clk) begin
  if (mg_start | ~mg_rdy)
    regJ <= regI + (mg_start ? regI : regJ);
  if (str_i)
    regI <= start ? 3 : (regI + (N+1)'(2));
  if (start)
    regNum <= num;
end

// N x 1 memory for storing bitmap of prime numbers
// primes[k] == 1 <=> k is a prime number

ram #(.ADDR_SZ(N), .DATA_SZ(1))
    not_prime_mem (clk, str_mem,
                   rdy ? k : N'(mg_start ? regI : regJ), // address
                   1'b1, not_prime);                   // data in and out

// FSM Controller

// enum { idle, start_mark, marking } state = idle, next;
typedef logic [1:0] State;

wire State idle = 2'b00;
wire State start_mark = 2'b01;
wire State marking = 2'b10;
State state;
State next;

initial state = idle;

always_ff @(negedge clk)
  state <= next;

// always @(state, start, rdy, mg_rdy, not_prime)
always_comb begin
  case (state)
    idle:
      casez ({start, mg_rdy, rdy, not_prime})
        4'b0???: { mg_start, str_i, str_mem, next } = { 3'b000, idle };
        4'b1???: { mg_start, str_i, str_mem, next } = { 3'b010, start_mark };
      endcase
    start_mark:
      casez ({start, mg_rdy, rdy, not_prime})
        // regI is prime
        4'b??00: { mg_start, str_i, str_mem, next } = { 3'b100, marking };
        // regI is not prime
        4'b??01: { mg_start, str_i, str_mem, next } = { 3'b110, start_mark };
        // regI > n, finished
        4'b??1?: { mg_start, str_i, str_mem, next } = { 3'b000, idle };
      endcase
    marking:
      casez ({start, mg_rdy, rdy, not_prime})
        // regJ <= n
        4'b?0??: { mg_start, str_i, str_mem, next } = { 3'b001, marking };
        // regJ > n
        4'b?1??: { mg_start, str_i, str_mem, next } = { 3'b010, start_mark };
      endcase
    default:
      { mg_start, str_i, str_mem, next } = 'x;
  endcase
end

endmodule
