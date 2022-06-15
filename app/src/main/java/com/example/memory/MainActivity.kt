package com.example.memory

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.memory.databinding.ActivityMainBinding
import kotlinx.coroutines.*

/**
 * Main Activity
 */
class MainActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ActivityMainBinding

    // colors
    private var colorBackground: Int = 0
    private var colorYellowSI: Int = 0
    private var colorPurpleSI: Int = 0

    // default image
    private var defaultImage: Int = 0

    // counters
    private var openCards = 0
    private var nrOfTries: Int = 1

    // last opened card
    private var lastOpened: Int = -1
    private var lastIndex: Int = -1

    // lists
    private lateinit var imageSrcList: List<Int>
    private var cardsList: MutableList<ImageButton> = mutableListOf()

    // jobs
    private var comparingCardsJob: Job? = null
    private var placingCardsJobs: Job? = null

    /**
     * Lifecycle method on Create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get colors
        colorBackground = ContextCompat.getColor(this, R.color.background_2)
        colorYellowSI = ContextCompat.getColor(this, R.color.SI_Yellow)
        colorPurpleSI = ContextCompat.getColor(this, R.color.SI_Purple)

        // get default image
        defaultImage = R.drawable.memory_deckblatt

        // fill image source list
        imageSrcList = listOf(
            R.drawable.astronaut,
            R.drawable.auto,
            R.drawable.ball,
            R.drawable.berge,
            R.drawable.haus,
            R.drawable.hirsch,
            R.drawable.jet,
            R.drawable.mond,
            R.drawable.palmen,
            R.drawable.strand,
            R.drawable.vogel,
            R.drawable.wald
        )

        // hide text "sub text" initially
        binding.tvSubText.visibility = View.INVISIBLE

        // add START btn click listener
        binding.btStart.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                // hide START btn & emoji
                binding.btStart.visibility = View.GONE
                binding.emoji.visibility = View.INVISIBLE

                // distribute cards
                distributeCards()

                // wait for cards to be distributed
                placingCardsJobs?.join()

                // let student fill imagesList
                val imagesList = MemoryLogic().assignImages(cardsList.size, imageSrcList)

                // set click listeners
                setClickListeners(cardsList, imagesList)
            }
        }
    }

    /**
     * resets cards to display default image
     */
    private fun resetCards() {

        // create lambda to set image resource
        val hideCard: (ImageButton) -> Unit = { card -> card.setImageResource(defaultImage) }

        // pass task to student
        MemoryLogic().hideAllCards(cardsList, hideCard)
    }

    /**
     * prepares playing field and distributes cards in grid
     */
    private suspend fun distributeCards() {

        // grid dimensions
        val columns = 4
        val rows = 6

        // make text "sub text" visible
        binding.tvSubText.visibility = View.VISIBLE

        // calculate card side length depending on grid width
        val gridWidth = binding.gridLayout.width
        val cardSide = gridWidth / columns

        // calculate grid height and set grid parameters
        val gridParams = binding.gridLayout.layoutParams
        gridParams.height =
            rows * cardSide + binding.gridLayout.paddingTop + binding.gridLayout.paddingBottom
        binding.gridLayout.layoutParams = gridParams
        binding.gridLayout.columnCount = columns
        binding.gridLayout.rowCount = rows

        // lambda to pass to student
        val placeCard: (Int, Int) -> Unit = { c, r ->
            placingCardsJobs = lifecycleScope.launch(Dispatchers.Main) {

                // set ImageButton parameters
                val padding = 4
                val ibCard = ImageButton(applicationContext)
                ibCard.setBackgroundColor(colorBackground)
                ibCard.setPadding(padding, padding, padding, padding)
                ibCard.scaleType = ImageView.ScaleType.FIT_CENTER
                ibCard.setImageResource(defaultImage)

                // set GridLayout parameters
                val glParams = GridLayout.LayoutParams()
                glParams.rowSpec = GridLayout.spec(r, 1f)
                glParams.columnSpec = GridLayout.spec(c, 1f)
                glParams.width = cardSide
                glParams.height = cardSide
                ibCard.layoutParams = glParams

                // place card in list and grid
                cardsList.add(ibCard)
                binding.gridLayout.addView(ibCard)
            }
        }

        // pass task to student
        MemoryLogic().distributeCards(columns, rows, placeCard)
    }

    /**
     * set click listeners for all cards
     */
    private fun setClickListeners(cards: MutableList<ImageButton>, images: MutableList<Int>) {

        // go through all card indices
        for (i in cards.indices) {

            // set click listener
            cards[i].setOnClickListener {

                // shouldn't compare cards if matched cards aren't deleted yet
                if (comparingCardsJob == null || !comparingCardsJob!!.isActive) {

                    // shouldn't compare cards if the same card is selected twice
                    if (lastIndex != i) {

                        // not more than two cards should be displayed at once
                        if (openCards >= 2) {
                            resetCards()
                            openCards = 0
                            lastOpened = 0
                            nrOfTries++
                        }
                    }

                    // lambda setImage
                    val setImage: (Int) -> Unit = { i -> cards[i].setImageResource(images[i]) }

                    // lambda makeBothCardsVisible
                    val makeBothCardInvisible: () -> Unit = {

                        // make cards invisible
                        cards[i].visibility = View.INVISIBLE
                        cards[lastIndex].visibility = View.INVISIBLE

                        // add to found pics
                        val imgView = ImageView(applicationContext)
                        imgView.setImageResource(images[i])
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.weight = 1f
                        binding.llPairsFound.addView(imgView, params)

                        // check if won
                        checkIfFoundAll()
                    }

                    // lambda compareImages
                    val compareImages: (Int, Int) -> Unit = { _, _ ->

                        // launches coroutine to check and display match for a while before
                        // moving card into relative layout
                        comparingCardsJob = lifecycleScope.launch {

                            // compares images of last opened and current card
                            val matchingCardsJob = launch {

                                if (lastOpened != 0) {

                                    // pass task to student
                                    MemoryLogic().compareCardImages(lastOpened, images[i], makeBothCardInvisible)

                                }
                            }

                            // finish job before changing index & last opened values
                            matchingCardsJob.join()

                            // in any case (matching or not) update
                            lastOpened = images[i]
                            openCards++
                            lastIndex = i

                        }
                    }

                    // pass task to student
                    MemoryLogic().cardClicked(lastIndex, i, setImage, compareImages)
                }
            }
        }
    }

    /**
     * check if all pairs were found
     */
    private fun checkIfFoundAll() {

        // pass task to student
        val foundAll = MemoryLogic().checkIfFoundAll(cardsList)

        // if all found set emoji & text
        if (foundAll) {

            binding.emoji.textSize = 150f
            binding.emoji.text = resources.getText(R.string.fireworks)
            binding.emoji.visibility = View.VISIBLE

            binding.tvSubText.text = resources.getString(R.string.won, nrOfTries)
            binding.tvSubText.setTextColor(colorPurpleSI)
        }
    }
}
