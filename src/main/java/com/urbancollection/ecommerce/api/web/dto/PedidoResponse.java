package com.urbancollection.ecommerce.api.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private Long id;
    private Long usuarioId;
    private Long direccionId;
    private Long cuponId; // opcional: id del cupón aplicado
    private String estado;
    private String metodoPago; // método de pago utilizado
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal envio;
    private BigDecimal total;
    private Integer cantidadTotal; //  cantidad total de items
    private LocalDateTime fecha;
    private List<ItemPedidoResponse> items;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getDireccionId() {
        return direccionId;
    }

    public void setDireccionId(Long direccionId) {
        this.direccionId = direccionId;
    }

    public Long getCuponId() {
        return cuponId;
    }

    public void setCuponId(Long cuponId) {
        this.cuponId = cuponId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    //  Getter y Setter para metodoPago
    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getEnvio() {
        return envio;
    }

    public void setEnvio(BigDecimal envio) {
        this.envio = envio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    // ✅ NUEVO: Getter y Setter para cantidadTotal
    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<ItemPedidoResponse> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoResponse> items) {
        this.items = items;
    }
}