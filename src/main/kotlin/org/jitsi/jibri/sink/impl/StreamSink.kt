/*
 * Copyright @ 2018 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jitsi.jibri.sink.impl

import org.jitsi.jibri.sink.Sink
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.nio.file.Path

/**
 * [StreamSink] represents a sink which will write to a network stream
 */
class StreamSink(val url: String, val streamingMaxBitrate: Int, val streamingBufSize: Int, val callName: String, recordingsDirectory: Path) : Sink {
    val extension: String = "mp4"
    val filepath: Path
    init {
        val suffix = "_${LocalDateTime.now().format(TIMESTAMP_FORMATTER)}.$extension"
        val filename = "${callName.take(MAX_FILENAME_LENGTH - suffix.length)}$suffix"
        filepath = recordingsDirectory.resolve(filename)
    }
    override val format: String = "tee"
    override val path: String = "[f=flv:onfail=ignore]$url|[f=mp4:onfail=ignore]$filepath"
    override val options: Array<String> = arrayOf(
        "-maxrate", "${streamingMaxBitrate}k",
        "-bufsize", "${streamingBufSize}k",
        "-flags", "+global_header",
        "-map", "0:v",
        "-map", "1:a"
    )

    companion object {
        private val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
        const val MAX_FILENAME_LENGTH = 125
    }
}
