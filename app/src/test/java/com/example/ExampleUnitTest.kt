package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun timecode_formatting_isCorrect() {
    val formatted = com.example.ui.screens.formatTimecode(65.25f)
    assertEquals("01:05.25", formatted)
  }

  @Test
  fun keyframe_interpolation_isCorrect() {
    val kf1 = com.example.model.Keyframe(timeSec = 0f, scale = 1.0f)
    val kf2 = com.example.model.Keyframe(timeSec = 10f, scale = 2.0f)
    val result = com.example.ui.screens.interpolateKeyframe(5.0f, listOf(kf1, kf2))
    assertEquals(1.5f, result.scale, 0.01f)
  }
}
