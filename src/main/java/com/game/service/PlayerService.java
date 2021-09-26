package com.game.service;

import com.game.entity.Player;

import java.util.List;

public interface PlayerService {

    List<Player> getAllPlayers();
    Integer getCountPlayers();
    Player createPlayer(Player player);
    Player getPlayer(Long id);
    Player updatePlayer(Long id, Player player);
    void deletePlayer(Long id);

}
