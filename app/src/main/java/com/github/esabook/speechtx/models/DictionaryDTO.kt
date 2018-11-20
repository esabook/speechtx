package com.github.esabook.speechtx.models

import java.io.Serializable

class DictionaryDTO : Serializable {
    var word: String? = null
    var frequency: Int = 0
    var highlighted: Boolean = false
}
