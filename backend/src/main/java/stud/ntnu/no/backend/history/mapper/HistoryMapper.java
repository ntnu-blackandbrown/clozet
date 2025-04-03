package stud.ntnu.no.backend.history.mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.entity.History;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HistoryMapper {

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
    
    public List<HistoryDTO> toDtoList(List<History> histories) {
        return histories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}