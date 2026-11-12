package mx.tvmaze;

record CommentView(String comment, int rating) {
    static CommentView from(ShowComment comment) {
        return new CommentView(comment.comment(), comment.rating());
    }
}
