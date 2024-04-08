package de.vatrascell.nezr.admin;

import de.vatrascell.nezr.location.Location;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Questionnaire {

    @Id
    private long questionnaireId;
    private LocalDateTime creationDate;
    private String name;
    private boolean isActive;
    private boolean isFinal;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

}
