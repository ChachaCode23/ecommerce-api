package com.urbancollection.ecommerce.api.web.dto;

import com.urbancollection.ecommerce.domain.enums.TipoDescuento;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CuponRequest
 * DTO para recibir datos de cupón desde la API (crear/actualizar).
 */
public class CuponRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 100, message = "El código no debe superar 100 caracteres")
    private String codigo;

    private boolean activo = true;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @DecimalMin(value = "0.00", message = "El mínimo de compra no puede ser negativo")
    private BigDecimal minimoCompra;

    @NotNull(message = "El tipo de descuento es obligatorio")
    private TipoDescuento tipo;

    @NotNull(message = "El valor de descuento es obligatorio")
    @DecimalMin(value = "0.00", message = "El valor de descuento no puede ser negativo")
    private BigDecimal valorDescuento;

    @DecimalMin(value = "0.00", message = "El tope de descuento no puede ser negativo")
    private BigDecimal topeDescuento;

    // Getters y setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public BigDecimal getMinimoCompra() { return minimoCompra; }
    public void setMinimoCompra(BigDecimal minimoCompra) { this.minimoCompra = minimoCompra; }

    public TipoDescuento getTipo() { return tipo; }
    public void setTipo(TipoDescuento tipo) { this.tipo = tipo; }

    public BigDecimal getValorDescuento() { return valorDescuento; }
    public void setValorDescuento(BigDecimal valorDescuento) { this.valorDescuento = valorDescuento; }

    public BigDecimal getTopeDescuento() { return topeDescuento; }
    public void setTopeDescuento(BigDecimal topeDescuento) { this.topeDescuento = topeDescuento; }
}