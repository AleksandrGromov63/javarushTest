package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/rest/players")
public class PlayerController {

    @Autowired
    private PlayerServiceImpl playerService;

    @Autowired
    private PlayerRepository playerRepository;

    private Date millisToDate(Long millis) {
        return (millis == null) ? null : new Date(millis);

    }
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Player>> getPlayers(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "title", required = false) String title,
                                                   @RequestParam(value = "race", required = false) Race race,
                                                   @RequestParam(value = "profession", required = false) Profession profession,
                                                   @RequestParam(value = "after", required = false) Long after,
                                                   @RequestParam(value = "before", required = false) Long before,
                                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                                   @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                                                   @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                                   @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ){


        List<Player> players = playerRepository.findAllByParams(name, title, race, profession,
                millisToDate(after), millisToDate(before),
                banned, minExperience, maxExperience,
                minLevel, maxLevel, PageRequest.of(pageNumber, pageSize));

        return new ResponseEntity<>(players, HttpStatus.OK);
    }


    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public ResponseEntity<Integer> countPlayerByParams(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "title", required = false) String title,
                                                   @RequestParam(value = "race", required = false) Race race,
                                                   @RequestParam(value = "profession", required = false) Profession profession,
                                                   @RequestParam(value = "after", required = false) Long after,
                                                   @RequestParam(value = "before", required = false) Long before,
                                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel){

        Integer countPlayerByParams = playerRepository.findAllByParams(name, title, race, profession,
                millisToDate(after), millisToDate(before),
                banned, minExperience, maxExperience,
                minLevel, maxLevel, null).size();

        return new ResponseEntity<>(countPlayerByParams, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {

        String name = player.getName();
        String title = player.getTitle();
        Race race = player.getRace();
        Profession profession = player.getProfession();
        Date birthday = player.getBirthday();
        Boolean banned = player.getBanned();
        Integer experience = player.getExperience();

        HttpHeaders headers = new HttpHeaders();

        if (birthday == null || birthday.getTime() < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthday);

        if (name == null || name == "" || name.length() > 12 || title == null || title.length() > 30
                || race == null || profession == null
                || calendar.get(Calendar.YEAR) < 2_000
                || calendar.get(Calendar.YEAR) > 3_000
                || experience == null || experience < 0 || experience > 10_000_000) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Integer level = (int) ((Math.sqrt(2500 + 200 * experience) - 50) / 100);

        Integer untilNextLevel = 50 * (level + 1) * (level + 2) - experience;

        Boolean ban;

        if (banned == null) ban = false;
        else ban = banned;

        Player createdPlayer = new Player(name, title, race, profession, birthday, ban,
                experience, level, untilNextLevel);

        playerService.createPlayer(createdPlayer);

        return new ResponseEntity<>(createdPlayer, headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long id) {
        if (id == null || id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!playerService.isExistPlayerById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Player player = this.playerService.getPlayer(id);

        return new ResponseEntity<>(player, HttpStatus.OK);

    }


    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody Player player) {

        if (id == null || id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!playerService.isExistPlayerById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();

        Player oldPlayer = playerService.getPlayer(id);

        String name = player.getName();
        String title = player.getTitle();
        Race race = player.getRace();
        Profession profession = player.getProfession();
        Date birthday = player.getBirthday();
        Boolean banned = player.getBanned();
        Integer experience = player.getExperience();

        Calendar calendar = Calendar.getInstance();
        if (birthday != null) {
            if (birthday.getTime() < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            calendar.setTime(birthday);

            if (calendar.get(Calendar.YEAR) < 2_000 || calendar.get(Calendar.YEAR) > 3_000)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else oldPlayer.setBirthday(birthday);
        }

        if (name != null) oldPlayer.setName(player.getName());
        if (title != null) oldPlayer.setTitle(player.getTitle());
        if (race != null) oldPlayer.setRace(player.getRace());
        if (profession != null) oldPlayer.setProfession(player.getProfession());
        if (birthday != null) oldPlayer.setBirthday(player.getBirthday());
        if (banned != null) oldPlayer.setBanned(player.getBanned());
        if (experience != null) {
            if (experience >= 0 && experience <= 10_000_000)
                oldPlayer.setExperience(player.getExperience());
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Integer level = (int) ((Math.sqrt(2500 + 200 * oldPlayer.getExperience()) - 50) / 100);

        Integer untilNextLevel = 50 * (level + 1) * (level + 2) - oldPlayer.getExperience();

        oldPlayer.setLevel(level);
        oldPlayer.setUntilNextLevel(untilNextLevel);

        playerService.updatePlayer(id, oldPlayer);

        return new ResponseEntity<>(oldPlayer, headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") Long id) {

        if (id == null || id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!playerService.isExistPlayerById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        playerService.deletePlayer(id);

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
