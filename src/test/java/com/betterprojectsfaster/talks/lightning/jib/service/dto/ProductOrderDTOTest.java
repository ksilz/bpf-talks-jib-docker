package com.betterprojectsfaster.talks.lightning.jib.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.betterprojectsfaster.talks.lightning.jib.web.rest.TestUtil;

public class ProductOrderDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductOrderDTO.class);
        ProductOrderDTO productOrderDTO1 = new ProductOrderDTO();
        productOrderDTO1.setId(1L);
        ProductOrderDTO productOrderDTO2 = new ProductOrderDTO();
        assertThat(productOrderDTO1).isNotEqualTo(productOrderDTO2);
        productOrderDTO2.setId(productOrderDTO1.getId());
        assertThat(productOrderDTO1).isEqualTo(productOrderDTO2);
        productOrderDTO2.setId(2L);
        assertThat(productOrderDTO1).isNotEqualTo(productOrderDTO2);
        productOrderDTO1.setId(null);
        assertThat(productOrderDTO1).isNotEqualTo(productOrderDTO2);
    }
}
