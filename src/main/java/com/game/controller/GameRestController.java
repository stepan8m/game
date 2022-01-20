package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("rest/players")
public class GameRestController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false) PlayerOrder order,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize
    ) {
        return new ResponseEntity<>(playerService.getPlayers(
                name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel,
                order,
                pageNumber,
                pageSize).getContent(), HttpStatus.OK);
    }

    @GetMapping("count")
    public ResponseEntity<Long> getCount(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false) PlayerOrder order,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize
    ) {
        return new ResponseEntity<>(playerService.getPlayers(
                name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel,
                order,
                pageNumber,
                pageSize).getTotalElements(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String id) {
        Long digitId = validateId(id);
        if (digitId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Player> optionalPlayer = playerService.getPlayerById(digitId);
        if (optionalPlayer.isPresent()) {
            return new ResponseEntity<>(optionalPlayer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        if (player.getBanned() == null) {
            player.setBanned(false);
        }
        if (!validatePlayer(player)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(playerService.savePlayer(player), HttpStatus.OK);
    }

    @PostMapping("{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable String id,
            @RequestBody Player player
            ) {
        Long digitId = validateId(id);
        if (digitId == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Optional<Player> updatePlayer = playerService.getPlayerById(digitId);
        if (!updatePlayer.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (player == null
                || (player.getName() == null
                && player.getTitle() == null
                && player.getRace() == null
                && player.getProfession() == null
                && player.getBanned() == null
                && player.getBirthday() == null
                && player.getExperience() == null)) {
            return new ResponseEntity<>(updatePlayer.get(), HttpStatus.OK);
        }

        if (player.getName() != null) updatePlayer.get().setName(player.getName());
        if (player.getTitle() != null) updatePlayer.get().setTitle(player.getTitle());
        if (player.getRace() != null) updatePlayer.get().setRace(player.getRace());
        if (player.getProfession() != null) updatePlayer.get().setProfession(player.getProfession());
        if (player.getExperience() != null) updatePlayer.get().setExperience(player.getExperience());
        if (player.getBanned() != null) updatePlayer.get().setBanned(player.getBanned());
        if (player.getBirthday() != null) updatePlayer.get().setBirthday(player.getBirthday());

        if (!validatePlayer(updatePlayer.get())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(playerService.savePlayer(updatePlayer.get()), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable String id) {
        Long digitId = validateId(id);
        if (digitId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (playerService.deletePlayerById(digitId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private boolean validatePlayer(Player player) {
        Date dateFrom = Date.from(LocalDateTime.of(2000, Month.JANUARY, 1,0,0).toInstant(OffsetDateTime.now().getOffset()));
        Date dateTo = Date.from(LocalDateTime.of(3000, Month.DECEMBER, 31,23,59).toInstant(OffsetDateTime.now().getOffset()));

        if (player.getName() == null
            || player.getName().isEmpty()
            || player.getName().length() > 12
            || player.getTitle().length() > 30
            || player.getExperience() < 0
            || player.getExperience() > 10000000
            || player.getBirthday() == null
            || player.getBirthday().getTime() < 0
            || player.getBirthday().before(dateFrom)
            || player.getBirthday().after(dateTo)) {
            return false;
        }
        return true;
    }

    private Long validateId(String id) {
        Long digitId;
        try {
            digitId = Long.parseLong(id);
            if (digitId < 1) return null;
        } catch (NumberFormatException ex) {
            return null;
        }
        return digitId;
    }
}
