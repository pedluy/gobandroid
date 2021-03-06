package org.ligi.gobandroid_hd.uitest

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.squareup.spoon.Spoon
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.TestApp
import org.ligi.gobandroid_hd.base.AssetAwareJunitTest
import org.ligi.gobandroid_hd.base.GobandroidTestBaseUtil
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TheReviewActivity : AssetAwareJunitTest() {

    @get:Rule
    val rule = ActivityTestRule(GameReviewActivity::class.java, true, false)

    @Inject
    lateinit var gameProvider: GameProvider

    private val activity: Activity by lazy { rule.launchActivity(null) }

    @Before
    fun setUp() {
        TestApp.component().inject(this)
        gameProvider.set(readGame("small_19x19"))
        super.setUp(activity)
    }

    @Test
    fun testThatGoBoardIsThere() {
        Spoon.screenshot(activity, "review")
        onView(withId(R.id.go_board)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_next)).check(matches(isDisplayed()))
    }


    @Test
    fun testThatNextAndLastButNotPrevAndFirstControlsAreThereOnBeginning() {
        onView(withId(R.id.btn_next)).check(matches(isEnabled()))
        onView(withId(R.id.btn_last)).check(matches(isEnabled()))

        onView(withId(R.id.btn_prev)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_first)).check(matches(not(isEnabled())))
    }


    @Test
    fun testThatAllControlsAreThereInTheMiddleOfTheGame() {
        onView(withId(R.id.btn_next)).perform(click())

        onView(withId(R.id.btn_next)).check(matches(isEnabled()))
        onView(withId(R.id.btn_last)).check(matches(isEnabled()))

        onView(withId(R.id.btn_prev)).check(matches(isEnabled()))
        onView(withId(R.id.btn_first)).check(matches(isEnabled()))

    }

    @Test
    fun testThatNextAndLastButtonsButNotPrevAndFirstAreThereOnEndOfGame() {
        onView(withId(R.id.btn_last)).perform(click())

        onView(withId(R.id.btn_next)).check(matches(not(isEnabled())))
        onView(withId(R.id.btn_last)).check(matches(not(isEnabled())))

        onView(withId(R.id.btn_prev)).check(matches(isEnabled()))
        onView(withId(R.id.btn_first)).check(matches(isEnabled()))

    }


    @Test
    fun testThatNextWorks() {
        onView(withId(R.id.btn_next)).perform(click())

        assertThat(gameProvider.get().actMove.movePos).isEqualTo(1)
        onView(withId(R.id.btn_next)).perform(click())

        assertThat(gameProvider.get().actMove.movePos).isEqualTo(2)

    }


    @Test
    fun testThatLastAndFirstWorks() {
        onView(withId(R.id.btn_last)).perform(click())

        assertThat(gameProvider.get().actMove.nextMoveVariationCount).isLessThan(1)

        onView(withId(R.id.btn_first)).perform(click())

        assertThat(gameProvider.get().actMove.parent).isEqualTo(null)

    }

    @Test
    fun foo() {
        try {
            GobandroidTestBaseUtil.readAssetHowItShouldBe(InstrumentationRegistry.getInstrumentation().context, "sgf/small_19x19.sgf")

            junit.framework.Assert.fail("if this works again ( minify stripped it away) - happy failing test!")
        } catch (e: Throwable) {

        }
    }

}
