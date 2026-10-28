package com.blessed.blsd_api_bend.repository;

import com.blessed.blsd_api_bend.model.entity.MovimentacaoATT;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimentacaoRepositoryATT extends JpaRepository<MovimentacaoATT, Long> {
    List<MovimentacaoATT> findTop10ByOrderByDataHoraDesc();
}