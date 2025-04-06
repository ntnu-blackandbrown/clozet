package stud.ntnu.no.backend.history.mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.entity.History;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between History entity and HistoryDTO.
 */
@Component
public class HistoryMapper {

    /**
     * Converts a History entity to a HistoryDTO.
     */
    public HistoryDTO toDto(History history) {
        HistoryDTO dto = new HistoryDTO();
        dto.setId(history.getId());
        dto.setUserId(history.getUser().getId());
        dto.setItemId(history.getItem().getId());
        dto.setItemTitle(history.getItem().getTitle());
        dto.setViewedAt(history.getViewedAt());
        dto.setActive(history.isActive());
        return dto;
    }

    /**
     * Converts a list of History entities to a list of HistoryDTOs.
     */
    public List<HistoryDTO> toDtoList(List<History> histories) {
        return histories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
