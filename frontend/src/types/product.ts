export interface Product {
  id: number
  title: string
  category: string
  location: string
  price: number
  image: string | null
  vippsPaymentEnabled: boolean
  wishlisted: boolean
}
