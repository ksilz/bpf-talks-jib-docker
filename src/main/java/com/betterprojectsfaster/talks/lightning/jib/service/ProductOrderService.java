package com.betterprojectsfaster.talks.lightning.jib.service;

import com.betterprojectsfaster.talks.lightning.jib.service.dto.ProductOrderDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.betterprojectsfaster.talks.lightning.jib.domain.ProductOrder}.
 */
public interface ProductOrderService {

    /**
     * Save a productOrder.
     *
     * @param productOrderDTO the entity to save.
     * @return the persisted entity.
     */
    ProductOrderDTO save(ProductOrderDTO productOrderDTO);

    /**
     * Get all the productOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductOrderDTO> findAll(Pageable pageable);

    /**
     * Get the "id" productOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductOrderDTO> findOne(Long id);

    /**
     * Delete the "id" productOrder.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
