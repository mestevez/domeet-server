import { Model } from 'vue-mc'
import User from '@/model/user'

/**
 * Attendant model
 */
export default class Attendant extends Model {
  options () {
    return {
      methods: {
        'save': 'PUT',
        'update': 'PUT',
        'create': 'PUT'
      }
    }
  }

  // Attribute mutations.
  mutations () {
    return {
      user_id: (user) => new User(JSON.parse(JSON.stringify(user)))
    }
  }

  routes () {
    return {
      save: '/app/meet/{meet_id}/attend/{user_id}',
      delete: '/app/meet/{meet_id}/attend/{user_id}'
    }
  }

  getRouteParameters () {
    return { meet_id: this.meet_id, user_id: this.user_id.user_id }
  }
}
