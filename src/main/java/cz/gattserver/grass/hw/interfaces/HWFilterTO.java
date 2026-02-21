package cz.gattserver.grass.hw.interfaces;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
public class HWFilterTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8625890502765369542L;

    private Long id;
    private String name;
    private HWItemState state;
    private Long usedInId;
    private String usedInName;
    private String supervizedFor;
    private BigDecimal price;
    private LocalDate purchaseDateFrom;
    private LocalDate purchaseDateTo;
    private Set<String> types;
    private Boolean publicItem;
    private Long ignoreId;

}