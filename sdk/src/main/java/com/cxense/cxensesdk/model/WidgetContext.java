package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context for Widget. It is required for loading items.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class WidgetContext {
    private String url;
    private String pageclass;
    private String sentiment;
    private boolean recommending;
    private Map<String, String> categories;
    private List<String> keywords;
    private List<String> neighbors;
    private String referrer;
    private List<ContextParameter> parameters;

    public WidgetContext() {
    }

    /**
     * Initialize Widget context from {@link WidgetContext.Builder}.
     *
     * @param builder builder instance.
     */
    public WidgetContext(@NonNull Builder builder) {
        this();
        url = builder.url;
        pageclass = builder.pageclass;
        sentiment = builder.sentiment;
        recommending = builder.recommending;
        categories = Collections.unmodifiableMap(builder.categories);
        keywords = Collections.unmodifiableList(builder.keywords);
        neighbors = Collections.unmodifiableList(builder.neighbors);
        referrer = builder.referrer;
        parameters = Collections.unmodifiableList(builder.parameters);
    }

    /**
     * Gets the URL for context.
     *
     * @return URL.
     */
    @Nullable
    public String getUrl() {
        return url;
    }

    /**
     * Gets the pageclass of the current page.
     *
     * @return the pageclass of the current page.
     */
    @Nullable
    public String getPageclass() {
        return pageclass;
    }

    /**
     * Gets the sentiment of the current page.
     *
     * @return specified sentiment.
     */
    @Nullable
    public String getSentiment() {
        return sentiment;
    }

    /**
     * Gets the recs-recommending setting of the current page.
     *
     * @return recs-recommending setting.
     */
    public boolean isRecommending() {
        return recommending;
    }

    /**
     * Gets map for categories of the current page.
     *
     * @return unmodifiable map for categories.
     */
    @NonNull
    public Map<String, String> getCategories() {
        return categories;
    }

    /**
     * Gets list of keywords describing the context.
     *
     * @return unmodifiable list of keywords
     */
    @NonNull
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Gets list of article IDs already linked to near this widget.
     * By default we will try to avoid recommending the same articles.
     *
     * @return unmodifiable list of linked articles IDs.
     */
    @NonNull
    public List<String> getNeighbors() {
        return neighbors;
    }

    /**
     * Gets the URI of the document that linked to this page.
     *
     * @return referrer URI.
     */
    @Nullable
    public String getReferrer() {
        return referrer;
    }

    /**
     * Gets unmodifiable list of {@link ContextParameter} objects.
     * This is used for placeholders.
     *
     * @return Unmodifiable list of parameters.
     */
    @NonNull
    public List<ContextParameter> getParameters() {
        return parameters;
    }

    public static class Builder {
        String url;
        String pageclass;
        String sentiment;
        boolean recommending;
        Map<String, String> categories = new HashMap<>();
        List<String> keywords = new ArrayList<>();
        List<String> neighbors = new ArrayList<>();
        String referrer;
        List<ContextParameter> parameters = new ArrayList<>();

        /**
         * Initialize builder
         *
         * @param url url for widget
         */
        public Builder(@Nullable String url) {
            this.url = url;
        }

        /**
         * Sets URL for widget
         *
         * @param url new URL
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setUrl(@Nullable String url) {
            this.url = url;
            return this;
        }

        /**
         * Set pageclass for widget.
         *
         * @param pageclass the pageclass of the current page.
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setPageclass(@Nullable String pageclass) {
            this.pageclass = pageclass;
            return this;
        }

        /**
         * Set sentiment for widget.
         *
         * @param sentiment the sentiment of the current page.
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setSentiment(@Nullable String sentiment) {
            this.sentiment = sentiment;
            return this;
        }

        /**
         * Set recs-recommending setting for widget.
         *
         * @param recommending recs-recommending setting of the current page.
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setRecommending(@Nullable boolean recommending) {
            this.recommending = recommending;
            return this;
        }

        /**
         * Set categories for widget.
         *
         * @param categories map for categories of the current page.
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setCategories(@NonNull Map<String, String> categories) {
            this.categories = new HashMap<>(categories);
            return this;
        }

        /**
         * Set list of keywords for widget.
         *
         * @param keywords list of keywords describing the context.
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setKeywords(@NonNull Collection<String> keywords) {
            this.keywords = new ArrayList<>(keywords);
            return this;
        }

        /**
         * Set list of article IDs for widget.
         *
         * @param neighbors list of linked articles IDs.
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setNeighbors(@NonNull Collection<String> neighbors) {
            this.neighbors = new ArrayList<>(neighbors);
            return this;
        }

        /**
         * Set referrer for widget.
         *
         * @param referrer referrer URI.
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setReferrer(@Nullable String referrer) {
            this.referrer = referrer;
            return this;
        }

        /**
         * Set parameters for widget.
         * This is used for placeholders.
         *
         * @param parameters list of {@link ContextParameter} objects.
         * @return {@link Builder} instance
         */
        @NonNull
        public WidgetContext.Builder setParameters(@NonNull Collection<ContextParameter> parameters) {
            this.parameters = new ArrayList<>(parameters);
            return this;
        }

        /**
         * Build widget context
         *
         * @return {@link WidgetContext} instance
         */
        @NonNull
        public WidgetContext build() {
            return new WidgetContext(this);
        }
    }
}
