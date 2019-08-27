const functions = require('firebase-functions');
const admin = require('firebase-admin');

const db = admin.firestore();
// [START_EXCLUDE]
const settings = {timestampsInSnapshots: true};
db.settings(settings);
// [END_EXCLUDE]

// [START aggregate_function]
exports.aggregateStats= functions.firestore
    .document('clients/{clientId}')
    .onWrite((change, context) => {
      // Get value of the newly added rating
      var birthDate = change.after.data().birthDate;

      // Get a reference to the restaurant
      var clientRef = db.collection('clients').doc(context.params.clientId);

      // Update aggregations in a transaction
      return db.runTransaction(transaction => {
        return transaction.get(clientRef).then(restDoc => {
          // Compute new number of ratings
          var newNumRatings = restDoc.data().numRatings + 1;

          // Compute new average rating
          var oldRatingTotal = restDoc.data().avgRating * restDoc.data().numRatings;
          var newAvgRating = (oldRatingTotal + ratingVal) / newNumRatings;

          // Update restaurant info
          return transaction.update(restRef, {
            avgRating: newAvgRating,
            numRatings: newNumRatings
          });
        });
      });
    });
// [END aggregate_function]