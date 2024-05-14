/*
 * scala-swing (https://www.scala-lang.org)
 *
 * Copyright EPFL, Lightbend, Inc., contributors
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package scala.swing
package event

/**
 * Enumeration of key codes used by key events.
 */
object Key extends Enumeration:

    object Location extends Enumeration:
        import java.awt.event.KeyEvent.*
        val Left: Location.Value     = Value(KEY_LOCATION_LEFT)
        val Right: Location.Value    = Value(KEY_LOCATION_RIGHT)
        val Numpad: Location.Value   = Value(KEY_LOCATION_NUMPAD)
        val Standard: Location.Value = Value(KEY_LOCATION_STANDARD)
        val Unknown: Location.Value  = Value(KEY_LOCATION_UNKNOWN)
    end Location

    type Modifiers = Int

    object Modifier:
        import java.awt.event.InputEvent.*
        val Shift: Modifiers    = SHIFT_DOWN_MASK
        val Control: Modifiers  = CTRL_DOWN_MASK
        val Alt: Modifiers      = ALT_DOWN_MASK
        val AltGraph: Modifiers = ALT_GRAPH_DOWN_MASK
        val Meta: Modifiers     = META_DOWN_MASK

        def text(mods: Int): String = java.awt.event.KeyEvent.getKeyModifiersText(mods)
    end Modifier

    // def text(k: Value) = java.awt.event.KeyEvent.getKeyText(k.id)

    import Key.{Value as KV}
    import java.awt.event.KeyEvent.{getKeyText as t, *}

    val Shift: KV    = Value(VK_SHIFT, t(VK_SHIFT))
    val Control: KV  = Value(VK_CONTROL, t(VK_CONTROL))
    val Alt: KV      = Value(VK_ALT, t(VK_ALT))
    val AltGraph: KV = Value(VK_ALT_GRAPH, t(VK_ALT_GRAPH))
    val Meta: KV     = Value(VK_META, t(VK_META))

    val Enter: KV     = Value(VK_ENTER, t(VK_ENTER))
    val BackSpace: KV = Value(VK_BACK_SPACE, t(VK_BACK_SPACE))
    val Tab: KV       = Value(VK_TAB, t(VK_TAB))
    val Cancel: KV    = Value(VK_CANCEL, t(VK_CANCEL))
    val Clear: KV     = Value(VK_CLEAR, t(VK_CLEAR))

    val Pause: KV                   = Value(VK_PAUSE, t(VK_PAUSE))
    val CapsLock: KV                = Value(VK_CAPS_LOCK, t(VK_CAPS_LOCK))
    val Escape: KV                  = Value(VK_ESCAPE, t(VK_ESCAPE))
    val Space: KV                   = Value(VK_SPACE, t(VK_SPACE))
    val PageUp: KV                  = Value(VK_PAGE_UP, t(VK_PAGE_UP))
    val PageDown: KV                = Value(VK_PAGE_DOWN, t(VK_PAGE_DOWN))
    val End: KV                     = Value(VK_END, t(VK_END))
    val Home: KV                    = Value(VK_HOME, t(VK_HOME))
    val Left: KV                    = Value(VK_LEFT, t(VK_LEFT))
    val Up: KV                      = Value(VK_UP, t(VK_UP))
    val Right: KV                   = Value(VK_RIGHT, t(VK_RIGHT))
    val Down: KV                    = Value(VK_DOWN, t(VK_DOWN))
    val Comma: KV                   = Value(VK_COMMA, t(VK_COMMA))
    val Minus: KV                   = Value(VK_MINUS, t(VK_MINUS))
    val Period: KV                  = Value(VK_PERIOD, t(VK_PERIOD))
    val Slash: KV                   = Value(VK_SLASH, t(VK_SLASH))
    val Key0: KV                    = Value(VK_0, t(VK_0))
    val Key1: KV                    = Value(VK_1, t(VK_1))
    val Key2: KV                    = Value(VK_2, t(VK_2))
    val Key3: KV                    = Value(VK_3, t(VK_3))
    val Key4: KV                    = Value(VK_4, t(VK_4))
    val Key5: KV                    = Value(VK_5, t(VK_5))
    val Key6: KV                    = Value(VK_6, t(VK_6))
    val Key7: KV                    = Value(VK_7, t(VK_7))
    val Key8: KV                    = Value(VK_8, t(VK_8))
    val Key9: KV                    = Value(VK_9, t(VK_9))
    val Semicolon: KV               = Value(VK_SEMICOLON, t(VK_SEMICOLON))
    val Equals: KV                  = Value(VK_EQUALS, t(VK_EQUALS))
    val A: KV                       = Value(VK_A, t(VK_A))
    val B: KV                       = Value(VK_B, t(VK_B))
    val C: KV                       = Value(VK_C, t(VK_C))
    val D: KV                       = Value(VK_D, t(VK_D))
    val E: KV                       = Value(VK_E, t(VK_E))
    val F: KV                       = Value(VK_F, t(VK_F))
    val G: KV                       = Value(VK_G, t(VK_G))
    val H: KV                       = Value(VK_H, t(VK_H))
    val I: KV                       = Value(VK_I, t(VK_I))
    val J: KV                       = Value(VK_J, t(VK_J))
    val K: KV                       = Value(VK_K, t(VK_K))
    val L: KV                       = Value(VK_L, t(VK_L))
    val M: KV                       = Value(VK_M, t(VK_M))
    val N: KV                       = Value(VK_N, t(VK_N))
    val O: KV                       = Value(VK_O, t(VK_O))
    val P: KV                       = Value(VK_P, t(VK_P))
    val Q: KV                       = Value(VK_Q, t(VK_Q))
    val R: KV                       = Value(VK_R, t(VK_R))
    val S: KV                       = Value(VK_S, t(VK_S))
    val T: KV                       = Value(VK_T, t(VK_T))
    val U: KV                       = Value(VK_U, t(VK_U))
    val V: KV                       = Value(VK_V, t(VK_V))
    val W: KV                       = Value(VK_W, t(VK_W))
    val X: KV                       = Value(VK_X, t(VK_X))
    val Y: KV                       = Value(VK_Y, t(VK_Y))
    val Z: KV                       = Value(VK_Z, t(VK_Z))
    val OpenBracket: KV             = Value(VK_OPEN_BRACKET, t(VK_OPEN_BRACKET))
    val BackSlash: KV               = Value(VK_BACK_SLASH, t(VK_BACK_SLASH))
    val CloseBracket: KV            = Value(VK_CLOSE_BRACKET, t(VK_CLOSE_BRACKET))
    val Numpad0: KV                 = Value(VK_NUMPAD0, t(VK_NUMPAD0))
    val Numpad1: KV                 = Value(VK_NUMPAD1, t(VK_NUMPAD1))
    val Numpad2: KV                 = Value(VK_NUMPAD2, t(VK_NUMPAD2))
    val Numpad3: KV                 = Value(VK_NUMPAD3, t(VK_NUMPAD3))
    val Numpad4: KV                 = Value(VK_NUMPAD4, t(VK_NUMPAD4))
    val Numpad5: KV                 = Value(VK_NUMPAD5, t(VK_NUMPAD5))
    val Numpad6: KV                 = Value(VK_NUMPAD6, t(VK_NUMPAD6))
    val Numpad7: KV                 = Value(VK_NUMPAD7, t(VK_NUMPAD7))
    val Numpad8: KV                 = Value(VK_NUMPAD8, t(VK_NUMPAD8))
    val Numpad9: KV                 = Value(VK_NUMPAD9, t(VK_NUMPAD9))
    val Multiply: KV                = Value(VK_MULTIPLY, t(VK_MULTIPLY))
    val Add: KV                     = Value(VK_ADD, t(VK_ADD))
    val Separator: KV               = Value(VK_SEPARATOR, t(VK_SEPARATOR))
    val Subtract: KV                = Value(VK_SUBTRACT, t(VK_SUBTRACT))
    val Decimal: KV                 = Value(VK_DECIMAL, t(VK_DECIMAL))
    val Divide: KV                  = Value(VK_DIVIDE, t(VK_DIVIDE))
    val Delete: KV                  = Value(VK_DELETE, t(VK_DELETE))
    val NumLock: KV                 = Value(VK_NUM_LOCK, t(VK_NUM_LOCK))
    val ScrollLock: KV              = Value(VK_SCROLL_LOCK, t(VK_SCROLL_LOCK))
    val F1: KV                      = Value(VK_F1, t(VK_F1))
    val F2: KV                      = Value(VK_F2, t(VK_F2))
    val F3: KV                      = Value(VK_F3, t(VK_F3))
    val F4: KV                      = Value(VK_F4, t(VK_F4))
    val F5: KV                      = Value(VK_F5, t(VK_F5))
    val F6: KV                      = Value(VK_F6, t(VK_F6))
    val F7: KV                      = Value(VK_F7, t(VK_F7))
    val F8: KV                      = Value(VK_F8, t(VK_F8))
    val F9: KV                      = Value(VK_F9, t(VK_F9))
    val F10: KV                     = Value(VK_F10, t(VK_F10))
    val F11: KV                     = Value(VK_F11, t(VK_F11))
    val F12: KV                     = Value(VK_F12, t(VK_F12))
    val F13: KV                     = Value(VK_F13, t(VK_F13))
    val F14: KV                     = Value(VK_F14, t(VK_F14))
    val F15: KV                     = Value(VK_F15, t(VK_F15))
    val F16: KV                     = Value(VK_F16, t(VK_F16))
    val F17: KV                     = Value(VK_F17, t(VK_F17))
    val F18: KV                     = Value(VK_F18, t(VK_F18))
    val F19: KV                     = Value(VK_F19, t(VK_F19))
    val F20: KV                     = Value(VK_F20, t(VK_F20))
    val F21: KV                     = Value(VK_F21, t(VK_F21))
    val F22: KV                     = Value(VK_F22, t(VK_F22))
    val F23: KV                     = Value(VK_F23, t(VK_F23))
    val F24: KV                     = Value(VK_F24, t(VK_F24))
    val Printscreen: KV             = Value(VK_PRINTSCREEN, t(VK_PRINTSCREEN))
    val Insert: KV                  = Value(VK_INSERT, t(VK_INSERT))
    val Help: KV                    = Value(VK_HELP, t(VK_HELP))
    val BackQuote: KV               = Value(VK_BACK_QUOTE, t(VK_BACK_QUOTE))
    val Quote: KV                   = Value(VK_QUOTE, t(VK_QUOTE))
    val KpUp: KV                    = Value(VK_KP_UP, t(VK_KP_UP))
    val KpDown: KV                  = Value(VK_KP_DOWN, t(VK_KP_DOWN))
    val KpLeft: KV                  = Value(VK_KP_LEFT, t(VK_KP_LEFT))
    val KpRight: KV                 = Value(VK_KP_RIGHT, t(VK_KP_RIGHT))
    val DeadGrave: KV               = Value(VK_DEAD_GRAVE, t(VK_DEAD_GRAVE))
    val DeadAcute: KV               = Value(VK_DEAD_ACUTE, t(VK_DEAD_ACUTE))
    val DeadCircumflex: KV          = Value(VK_DEAD_CIRCUMFLEX, t(VK_DEAD_CIRCUMFLEX))
    val DeadTilde: KV               = Value(VK_DEAD_TILDE, t(VK_DEAD_TILDE))
    val DeadMacron: KV              = Value(VK_DEAD_MACRON, t(VK_DEAD_MACRON))
    val DeadBreve: KV               = Value(VK_DEAD_BREVE, t(VK_DEAD_BREVE))
    val DeadAbovedot: KV            = Value(VK_DEAD_ABOVEDOT, t(VK_DEAD_ABOVEDOT))
    val DeadDiaeresis: KV           = Value(VK_DEAD_DIAERESIS, t(VK_DEAD_DIAERESIS))
    val DeadAbovering: KV           = Value(VK_DEAD_ABOVERING, t(VK_DEAD_ABOVERING))
    val DeadDoubleacute: KV         = Value(VK_DEAD_DOUBLEACUTE, t(VK_DEAD_DOUBLEACUTE))
    val DeadCaron: KV               = Value(VK_DEAD_CARON, t(VK_DEAD_CARON))
    val DeadCedilla: KV             = Value(VK_DEAD_CEDILLA, t(VK_DEAD_CEDILLA))
    val DeadOgonek: KV              = Value(VK_DEAD_OGONEK, t(VK_DEAD_OGONEK))
    val DeadIota: KV                = Value(VK_DEAD_IOTA, t(VK_DEAD_IOTA))
    val DeadVoicedSound: KV         = Value(VK_DEAD_VOICED_SOUND, t(VK_DEAD_VOICED_SOUND))
    val DeadSemivoicedSound: KV     = Value(VK_DEAD_SEMIVOICED_SOUND, t(VK_DEAD_SEMIVOICED_SOUND))
    val Ampersand: KV               = Value(VK_AMPERSAND, t(VK_AMPERSAND))
    val Asterisk: KV                = Value(VK_ASTERISK, t(VK_ASTERISK))
    val Quotedbl: KV                = Value(VK_QUOTEDBL, t(VK_QUOTEDBL))
    val Less: KV                    = Value(VK_LESS, t(VK_LESS))
    val Greater: KV                 = Value(VK_GREATER, t(VK_GREATER))
    val Braceleft: KV               = Value(VK_BRACELEFT, t(VK_BRACELEFT))
    val Braceright: KV              = Value(VK_BRACERIGHT, t(VK_BRACERIGHT))
    val At: KV                      = Value(VK_AT, t(VK_AT))
    val Colon: KV                   = Value(VK_COLON, t(VK_COLON))
    val Circumflex: KV              = Value(VK_CIRCUMFLEX, t(VK_CIRCUMFLEX))
    val Dollar: KV                  = Value(VK_DOLLAR, t(VK_DOLLAR))
    val EuroSign: KV                = Value(VK_EURO_SIGN, t(VK_EURO_SIGN))
    val ExclamationMark: KV         = Value(VK_EXCLAMATION_MARK, t(VK_EXCLAMATION_MARK))
    val InvertedExclamationMark: KV = Value(VK_INVERTED_EXCLAMATION_MARK, t(VK_INVERTED_EXCLAMATION_MARK))
    val LeftParenthesis: KV         = Value(VK_LEFT_PARENTHESIS, t(VK_LEFT_PARENTHESIS))
    val NumberSign: KV              = Value(VK_NUMBER_SIGN, t(VK_NUMBER_SIGN))
    val Plus: KV                    = Value(VK_PLUS, t(VK_PLUS))
    val RightParenthesis: KV        = Value(VK_RIGHT_PARENTHESIS, t(VK_RIGHT_PARENTHESIS))
    val Underscore: KV              = Value(VK_UNDERSCORE, t(VK_UNDERSCORE))
    val Windows: KV                 = Value(VK_WINDOWS, t(VK_WINDOWS))
    val ContextMenu: KV             = Value(VK_CONTEXT_MENU, t(VK_CONTEXT_MENU))
    val Final: KV                   = Value(VK_FINAL, t(VK_FINAL))
    val Convert: KV                 = Value(VK_CONVERT, t(VK_CONVERT))
    val Nonconvert: KV              = Value(VK_NONCONVERT, t(VK_NONCONVERT))
    val Accept: KV                  = Value(VK_ACCEPT, t(VK_ACCEPT))
    val Modechange: KV              = Value(VK_MODECHANGE, t(VK_MODECHANGE))
    val Kana: KV                    = Value(VK_KANA, t(VK_KANA))
    val Kanji: KV                   = Value(VK_KANJI, t(VK_KANJI))
    val Alphanumeric: KV            = Value(VK_ALPHANUMERIC, t(VK_ALPHANUMERIC))
    val Katakana: KV                = Value(VK_KATAKANA, t(VK_KATAKANA))
    val Hiragana: KV                = Value(VK_HIRAGANA, t(VK_HIRAGANA))
    val FullWidth: KV               = Value(VK_FULL_WIDTH, t(VK_FULL_WIDTH))
    val HalfWidth: KV               = Value(VK_HALF_WIDTH, t(VK_HALF_WIDTH))
    val RomanCharacters: KV         = Value(VK_ROMAN_CHARACTERS, t(VK_ROMAN_CHARACTERS))
    val AllCandidates: KV           = Value(VK_ALL_CANDIDATES, t(VK_ALL_CANDIDATES))
    val PreviousCandidate: KV       = Value(VK_PREVIOUS_CANDIDATE, t(VK_PREVIOUS_CANDIDATE))
    val CodeInput: KV               = Value(VK_CODE_INPUT, t(VK_CODE_INPUT))
    val JapaneseKatakana: KV        = Value(VK_JAPANESE_KATAKANA, t(VK_JAPANESE_KATAKANA))
    val JapaneseHiragana: KV        = Value(VK_JAPANESE_HIRAGANA, t(VK_JAPANESE_HIRAGANA))
    val JapaneseRoman: KV           = Value(VK_JAPANESE_ROMAN, t(VK_JAPANESE_ROMAN))
    val KanaLock: KV                = Value(VK_KANA_LOCK, t(VK_KANA_LOCK))
    val InputMethodOnOff: KV        = Value(VK_INPUT_METHOD_ON_OFF, t(VK_INPUT_METHOD_ON_OFF))
    val Cut: KV                     = Value(VK_CUT, t(VK_CUT))
    val Copy: KV                    = Value(VK_COPY, t(VK_COPY))
    val Paste: KV                   = Value(VK_PASTE, t(VK_PASTE))
    val Undo: KV                    = Value(VK_UNDO, t(VK_UNDO))
    val Again: KV                   = Value(VK_AGAIN, t(VK_AGAIN))
    val Find: KV                    = Value(VK_FIND, t(VK_FIND))
    val Props: KV                   = Value(VK_PROPS, t(VK_PROPS))
    val Stop: KV                    = Value(VK_STOP, t(VK_STOP))
    val Compose: KV                 = Value(VK_COMPOSE, t(VK_COMPOSE))
    val Begin: KV                   = Value(VK_BEGIN, t(VK_BEGIN))
    val Undefined: KV               = Value(VK_UNDEFINED, t(VK_UNDEFINED))
end Key
