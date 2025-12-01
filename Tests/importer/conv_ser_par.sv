module conv_ser_par( input clk, in,
                     output reg [7:0] d,
                     output rdy );

    reg [2:0] counter;
    reg receive;
    
    assign rdy = (counter == 0) && (receive == 0); // counter llega a 8 y no se recibe nada
    initial receive = 0;

    // Actualización de estado y registros
    always_ff @(negedge clk) begin
        if (!receive && in == 0) begin  // si receive es 0 e in es 0
            receive <= 1;
            counter <= 0;
            d <= 8'b0;
        end
        else if (receive) begin         // si receive es 1, contar y actualizar
            d <= (d << 1) | (8)'(in);   // d desplazado + union con n
            counter <= counter + 3'b1;  // sumar al contador de bits
            if (counter == 7) begin     // si contador llega a 7
                receive <= 0;           // terminar recepción
            end
        end
    end
endmodule
