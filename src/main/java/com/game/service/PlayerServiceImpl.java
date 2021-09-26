package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService{

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Integer getCountPlayers() {
        return Math.toIntExact(playerRepository.count());
    }

    @Override
    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Player getPlayer(Long id) {
        return playerRepository.findById(id).get();
    }

    @Override
    public Player updatePlayer(Long id, Player player) {
        return playerRepository.save(player);
    }

    @Override
    public void deletePlayer(Long id) {
       playerRepository.deleteById(id);
    }

    public boolean isExistPlayerById(Long id){
        return playerRepository.existsById(id);
    }

    }


