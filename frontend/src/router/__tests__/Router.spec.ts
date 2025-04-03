import { createRouter, createWebHistory } from 'vue-router'
import router from '@/router/router'
import { describe, it, expect, beforeEach } from 'vitest'

describe('Router', () => {
  beforeEach(async () => {
    // Reset router to a known state before each test.
    router.push('/')
    await router.isReady()
  })

  it('should have a route named "home"', () => {
    const homeRoute = router.getRoutes().find((r) => r.name === 'home')
    expect(homeRoute).toBeDefined()
  })

  it('resolves the "create-product" route', async () => {
    await router.push('/create-product')
    await router.isReady()
    expect(router.currentRoute.value.name).toBe('create-product')
  })

  it('has nested profile routes including "profile-settings"', () => {
    const profileRoute = router.getRoutes().find((r) => r.name === 'profile')
    expect(profileRoute).toBeDefined()
    const children = profileRoute?.children
    expect(children).toBeDefined()
    const settingsRoute = children?.find((child) => child.name === 'profile-settings')
    expect(settingsRoute).toBeDefined()
  })
})
