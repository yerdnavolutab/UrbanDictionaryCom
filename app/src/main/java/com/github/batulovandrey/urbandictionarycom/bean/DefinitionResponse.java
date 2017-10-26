package com.github.batulovandrey.urbandictionarycom.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Andrey Batulov on 26/10/2017
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DefinitionResponse {

    @JsonProperty("definition")
    private String mDefinition;

    @JsonProperty("permalink")
    private String mPermalink;

    @JsonProperty("thumbs_up")
    private int mThumbsUp;

    @JsonProperty("author")
    private String mAuthor;

    @JsonProperty("word")
    private String mWord;

    @JsonProperty("defid")
    private long mDefId;

    @JsonProperty("example")
    private String mExample;

    @JsonProperty("thumbs_down")
    private int mThumbsDown;

    public DefinitionResponse() {
        // needed by Jackson
    }

    public String getDefinition() {
        return mDefinition;
    }

    public void setDefinition(String definition) {
        mDefinition = definition;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public void setPermalink(String permalink) {
        mPermalink = permalink;
    }

    public int getThumbsUp() {
        return mThumbsUp;
    }

    public void setThumbsUp(int thumbsUp) {
        mThumbsUp = thumbsUp;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String word) {
        mWord = word;
    }

    public long getDefId() {
        return mDefId;
    }

    public void setDefId(long defId) {
        mDefId = defId;
    }

    public String getExample() {
        return mExample;
    }

    public void setExample(String example) {
        mExample = example;
    }

    public int getThumbsDown() {
        return mThumbsDown;
    }

    public void setThumbsDown(int thumbsDown) {
        mThumbsDown = thumbsDown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefinitionResponse that = (DefinitionResponse) o;
        return mThumbsUp == that.mThumbsUp &&
                mDefId == that.mDefId &&
                mThumbsDown == that.mThumbsDown &&
                Objects.equal(mDefinition, that.mDefinition) &&
                Objects.equal(mPermalink, that.mPermalink) &&
                Objects.equal(mAuthor, that.mAuthor) &&
                Objects.equal(mWord, that.mWord) &&
                Objects.equal(mExample, that.mExample);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                mDefinition,
                mPermalink,
                mThumbsUp,
                mAuthor,
                mWord,
                mDefId,
                mExample,
                mThumbsDown);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mDefinition", mDefinition)
                .add("mPermalink", mPermalink)
                .add("mThumbsUp", mThumbsUp)
                .add("mAuthor", mAuthor)
                .add("mWord", mWord)
                .add("mDefId", mDefId)
                .add("mExample", mExample)
                .add("mThumbsDown", mThumbsDown)
                .toString();
    }
}