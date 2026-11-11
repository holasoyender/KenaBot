/*
 * File:          FilterManager.kt
 * Path:          v3\src\main\kotlin\managers\FilterManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-07 16:59:49 +02:00
 *
 * SHA-256: 2acfded2b954c82066574a58bbd0e056fe791588be3d759198690f039600de3e
 *
 */


package managers

import atenea.driver.io.filters.Karaoke
import atenea.driver.io.filters.Rotation
import atenea.driver.io.filters.Timescale
import atenea.driver.io.filters.Tremolo
import atenea.driver.player.AteneaPlayer

object FilterManager {

    fun setBassBoost(player: AteneaPlayer) {
        player.filters.setBand(1, (0.5).toFloat())
        player.filters.commit()
    }

    fun removeBassBoost(player: AteneaPlayer) {
        player.filters.setBand(1, (0).toFloat())
        player.filters.commit()
    }

    fun set8D(player: AteneaPlayer) {
        val rotation = Rotation()
        rotation.setFrequency(0.2F)

        player.filters.setRotation(rotation)
        player.filters.commit()
    }

    fun remove8D(player: AteneaPlayer) {
        player.filters.setRotation(null)
        player.filters.commit()
    }

    fun setNightCore(player: AteneaPlayer) {
        val timescale = Timescale()

        timescale.setSpeed(1.3F)
        timescale.setPitch(1.3F)
        timescale.setRate(1.0F)

        player.filters.setTimescale(timescale)
        player.filters.commit()
    }

    fun removeNightCore(player: AteneaPlayer) {
        player.filters.setTimescale(null)
        player.filters.commit()
    }

    fun setVaporWave(player: AteneaPlayer) {
        val timescale = Timescale()

        timescale.setSpeed(0.85F)
        timescale.setPitch(0.8F)
        timescale.setRate(1.0F)

        player.filters.setTimescale(timescale)
        player.filters.commit()
    }

    fun removeVaporWave(player: AteneaPlayer) {
        player.filters.setTimescale(null)
        player.filters.commit()
    }

    fun setTreble(player: AteneaPlayer) {
        player.filters.setBand(0, (0.6).toFloat())
        player.filters.setBand(1, (0.67).toFloat())
        player.filters.setBand(2, (0.67).toFloat())
        player.filters.setBand(3, (0).toFloat())
        player.filters.setBand(4, (-0.25).toFloat())
        player.filters.setBand(5, (0.15).toFloat())
        player.filters.setBand(6, (-0.25).toFloat())
        player.filters.setBand(7, (0.23).toFloat())
        player.filters.setBand(8, (0.35).toFloat())
        player.filters.setBand(9, (0.45).toFloat())
        player.filters.setBand(10, (0.55).toFloat())
        player.filters.setBand(11, (0.6).toFloat())
        player.filters.setBand(12, (0.55).toFloat())
        player.filters.setBand(13, (0).toFloat())

        player.filters.commit()
    }

    fun removeTreble(player: AteneaPlayer) {
        player.filters.setBand(0, (0).toFloat())
        player.filters.setBand(1, (0).toFloat())
        player.filters.setBand(2, (0).toFloat())
        player.filters.setBand(3, (0).toFloat())
        player.filters.setBand(4, (0).toFloat())
        player.filters.setBand(5, (0).toFloat())
        player.filters.setBand(6, (0).toFloat())
        player.filters.setBand(7, (0).toFloat())
        player.filters.setBand(8, (0).toFloat())
        player.filters.setBand(9, (0).toFloat())
        player.filters.setBand(10, (0).toFloat())
        player.filters.setBand(11, (0).toFloat())
        player.filters.setBand(12, (0).toFloat())
        player.filters.setBand(13, (0).toFloat())

        player.filters.commit()
    }

    fun setKaraoke(player: AteneaPlayer) {
        val karaoke = Karaoke()

        karaoke.setLevel((1.0).toFloat())
        karaoke.setMonoLevel((1.0).toFloat())
        karaoke.setFilterBand((220.0).toFloat())
        karaoke.setFilterWidth((100.0).toFloat())

        player.filters.setKaraoke(karaoke)
        player.filters.commit()
    }

    fun removeKaraoke(player: AteneaPlayer) {
        player.filters.setKaraoke(null)
        player.filters.commit()
    }

    fun setTremolo(player: AteneaPlayer) {
        val tremolo = Tremolo()

        tremolo.setFrequency((10).toFloat())
        tremolo.setDepth((0.5).toFloat())

        player.filters.setTremolo(tremolo)
        player.filters.commit()
    }

    fun removeTremolo(player: AteneaPlayer) {
        player.filters.setTremolo(null)
        player.filters.commit()
    }

    fun setSpeed(player: AteneaPlayer) {
        val timescale = Timescale()

        timescale.setSpeed((1.5010).toFloat())
        timescale.setPitch((1.2450).toFloat())
        timescale.setRate((1.9210F).toFloat())

        player.filters.setTimescale(timescale)
        player.filters.commit()
    }

    fun removeSpeed(player: AteneaPlayer) {
        player.filters.setTimescale(null)
        player.filters.commit()
    }

    fun setPitch(player: AteneaPlayer) {
        val timescale = Timescale()

        timescale.setSpeed((1.0F).toFloat())
        timescale.setPitch((1.5010F).toFloat())
        timescale.setRate((1.0F).toFloat())

        player.filters.setTimescale(timescale)
        player.filters.commit()
    }

    fun removePitch(player: AteneaPlayer) {
        player.filters.setTimescale(null)
        player.filters.commit()
    }
}