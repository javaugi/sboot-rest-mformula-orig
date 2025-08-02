// Save this in the system.js collection
db.system.js.save({
    _id: "updateBookRatings",
    value: function(minReviews) {
        db.books.find({
            "reviews.1": {$exists: true} // At least 2 reviews
        }).forEach(function(book) {
            var total = 0;
            book.reviews.forEach(function(review) {
                total += review.rating;
            });
            var avgRating = total / book.reviews.length;
            db.books.update(
                {_id: book._id},
                {$set: {averageRating: avgRating}}
            );
        });
        return "Ratings updated for books with at least " + minReviews + " reviews";
    }
});
