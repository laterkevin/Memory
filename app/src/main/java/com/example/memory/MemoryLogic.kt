package com.example.memory

import android.view.View
import android.widget.ImageButton
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Die Klasse MemoryLogic enthält eine Sammlung von Funktionen, welche die Kernlogik für das
 * Memory Spiel enthalten
 */
class MemoryLogic {

    /**
     * Diese Funktion geht alle Karten(ImageButtons) in der übergebenen Liste durch
     * und verdeckt jede Karte mit Hilfe des übergebenen Lambdas
     */
    fun hideAllCards(cards: List<ImageButton>, hideCard: (ImageButton) -> Unit) {

        // gehe alle Karten durch und verdecke verdecke jede Karte
        // todo Schreibe hier deinen Code

    }

    /**
     *  Diese Funktion geht alle Felder durch und platziert nacheinander, mit Hilfe des
     *  übergebenen Lambdas (= Pfeilfunktion) eine Karte auf jedem Feld
     */
    suspend fun distributeCards(columns: Int, rows: Int, placeCards: (Int, Int) -> Unit) {

        // gehe alle Spalten durch
        // todo Schreibe hier deinen Code

        // gehe alle Zeilen durch
        // todo Schreibe hier deinen Code

        // platziere Karte in Spalte, Zeile
        // todo Schreibe hier deinen Code

        // verzögere Ablauf um 50ms
        // todo Schreibe hier deinen Code

    }

    // jetzt sollten die Karten auf dem Spielfeld erscheinen

    /**
     * Diese Funktion soll bestimmen, was passiert wenn man eine Karte anklickt
     */
    fun cardClicked(lastIndex: Int, thisIndex: Int, setImage: (Int) -> Unit, compareImages: (Int, Int) -> Unit) {

        // stelle sicher, dass es sich nicht um die gleiche Karte handelt
        // todo Schreibe hier deinen Code

        // setze das Bild
        // vergleiche die Bilder
        // todo Schreibe hier deinen Code

    }

    /**
     * Diese Funktion füllt eine neue Liste mit Bildern aus der Bilderquelle, sodass jedes Bild
     * aus der Bilderquelle zwei mal in der neuen Liste vorkommt und liefert diese zurück
     * Die Reihenfolge ist dabei zufällig
     */
    fun assignImages(numberOfCards: Int, imageSource: List<Int>): MutableList<Int> {

        // Die neue Liste
        val cardImageIDs = mutableListOf<Int>()

        // todo Schreibe hier deinen Code

        // liefere die befüllte Liste zurück
        return cardImageIDs
    }

    /**
     * Diese Funktion vergleich die Bilder zweier Karten und lässt diese vom Spielfeld verschwinden
     */
    suspend fun compareCardImages(
        lastImage: Int,
        currentImage: Int,
        makeBothCardsInvisible: () -> Unit
    ) {

        // teste ob beide Bilder gleich sind
        // todo Schreibe hier deinen Code

    }

    /**
     * Diese Funktion prüft alle Karten in der übergebenen Liste auf Ihre Sichtbarkeit (visibility)
     * Liefert true zurück, falls es noch sichtbare Karten gibt
     */
    fun checkIfFoundAll(cards: List<ImageButton>): Boolean {

        // todo Schreibe hier deinen Code

        return false
    }
}
