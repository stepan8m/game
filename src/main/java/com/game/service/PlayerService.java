package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public Page<Player> getPlayers(String name,
                            String title,
                            Race race,
                            Profession profession,
                            Long after,
                            Long before,
                            Boolean banned,
                            Integer minExperience,
                            Integer maxExperience,
                            Integer minLevel,
                            Integer maxLevel,
                            PlayerOrder order,
                            Integer pageNumber,
                            Integer pageSize) {
        Specification<Player> spec = null;
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
        if (order == null) order = PlayerOrder.ID;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        if (name != null) {
            spec = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
        }
        if (title != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (race != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("race"), race);
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (profession != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("profession"), profession);
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (after != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.greaterThan(root.get("birthday"), new Date(after));
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (before != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.lessThan(root.get("birthday"), new Date(before));
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (banned != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("banned"), banned);
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (minExperience != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience);
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (maxExperience != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience);
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (minLevel != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel);
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (maxLevel != null) {
            Specification<Player> specTemp = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel);
            spec = spec == null ? specTemp : spec.and(specTemp);
        }
        if (spec != null) {
            return playerRepository.findAll(spec, pageable);
        }
        return playerRepository.findAll(pageable);
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public Player savePlayer(Player player) {
        Integer level = (int)(Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
        Integer untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();
        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevel);
        return playerRepository.save(player);
    }

    public boolean deletePlayerById(Long id) {
        if (playerRepository.findById(id).isPresent()) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
