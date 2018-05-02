import { Model } from 'vue-mc'

/**
 * Task model
 */
export default class Subject extends Model {
  options () {
    return {
      identifier: 'subject_id'
    }
  }

  // Attribute mutations.
  mutations () {
    return {
      subject_duration: Number
    }
  }

  routes () {
    return {
      save: '/app/meet/subject/{subject_id}',
      delete: '/app/meet/subject/{subject_id}'
    }
  }
}
