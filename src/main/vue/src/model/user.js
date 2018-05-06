import { Model } from 'vue-mc'

/**
 * User model
 */
export default class User extends Model {
  options () {
    return {
      identifier: 'user_id'
    }
  }
}
