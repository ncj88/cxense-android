package io.piano.android.cxense.model

import com.squareup.moshi.JsonClass

/**
 * Candidate segments to consider at retrieving a list of all segments where the user is a member.
 */
@JsonClass(generateAdapter = true)
class CandidateSegment(
    val id: String,
    val type: SegmentType,
) {
    init {
        require(id.isNotEmpty()) {
            "Segment id can't be empty"
        }
    }
}
