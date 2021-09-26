package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.text.html.parser.Entity;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Repository interface for (@link Player) class.
 */

@Repository
@Transactional
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("select pl from Player pl where " +
            "(:name is null or pl.name like concat('%',:name,'%')) and " +
            "(:title is null or pl.title like concat('%',:title,'%')) and " +
            "(:race is null or pl.race = :race) and " +
            "(:profession is null or pl.profession = :profession) and " +
            "(:after is null or pl.birthday >= :after) and " +
            "(:before is null or pl.birthday <= :before) and " +
            "(:banned is null or pl.banned = :banned) and " +
            "(:minExperience is null or pl.experience >= :minExperience) and " +
            "(:maxExperience is null or pl.experience <= :maxExperience) and " +
            "(:minLevel is null or pl.level >= :minLevel) and " +
            "(:maxLevel is null or pl.level <= :maxLevel)")
    List<Player> findAllByParams(
            @Param("name") String name,
            @Param("title") String title,
            @Param("race") Race race,
            @Param("profession") Profession profession,
            @Param("after") Date after, @Param("before") Date Before,
            @Param("banned") Boolean banned,
            @Param("minExperience") Integer minExperience, @Param("maxExperience") Integer maxExperience,
            @Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel,
            Pageable pageable);
}




