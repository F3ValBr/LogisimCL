`default_nettype none

module counter4b (
    output logic [3:0] value,
    input logic clk
);

    always_ff @(posedge clk) begin
        value <= value + 4'd1;
    end

endmodule
