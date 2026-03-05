package com.vira.paas.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.vira.paas.enums.ApplicationStatusEnum;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "git_url", nullable = false)
    private String gitUrl;

    @Column(name = "git_branch", nullable = false)
    @Builder.Default
    private String gitBranch = "main";

    @Column(name = "build_path", nullable = false)
    @Builder.Default
    private String buildPath = "./";

    @Enumerated(EnumType.STRING)
    private ApplicationStatusEnum status;

    private String lastImageTag;

    @Builder.Default
    private Integer replicas = 1;
    @Builder.Default
    private Double cpuLimit = 0.5;
    @Builder.Default
    private Integer memLimitMb = 512;
    @Builder.Default
    private Integer exposedPort = 8080;

    @Builder.Default
    private String storagePath = "/data";

    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "app_id")
    private List<EnvVariableModel> envVariables;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null)
            this.status = ApplicationStatusEnum.CREATED;
    }
}
