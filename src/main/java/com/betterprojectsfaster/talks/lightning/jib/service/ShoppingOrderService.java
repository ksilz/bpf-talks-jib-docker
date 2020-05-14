package com.betterprojectsfaster.talks.lightning.jib.service;

import com.betterprojectsfaster.talks.lightning.jib.service.dto.ShoppingOrderDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.betterprojectsfaster.talks.lightning.jib.domain.ShoppingOrder}.
 */
public interface ShoppingOrderService {

    /**
     * Save a shoppingOrder.
     *
     * @param shoppingOrderDTO the entity to save.
     * @return the persisted entity.
     */
    ShoppingOrderDTO save(ShoppingOrderDTO shoppingOrderDTO);

    /**
     * Get all the shoppingOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ShoppingOrderDTO> findAll(Pageable pageable);
    /**
     * Get all the ShoppingOrderDTO where Shipment is {@code null}.
     *
     * @return the list of entities.
     */
    List<ShoppingOrderDTO> findAllWhereShipmentIsNull();

    /**
     * Get the "id" shoppingOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShoppingOrderDTO> findOne(Long id);

    /**
     * Delete the "id" shoppingOrder.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
