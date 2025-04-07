export interface Product {
  id: number
  title: string
  category: string
  location: string
  price: number
  image: string | null
  vippsPaymentEnabled: boolean
  wishlisted: boolean
  brand?: string
  color?: string
  condition?: string
  size?: string
}

export interface ProductDisplay {
  id: number
  title: string
  category: string
  location: string
  price: number
  image: string | null
  vippsPaymentEnabled: boolean
  wishlisted: boolean
  brand?: string
  color?: string
  condition?: string
  size?: string
  longDescription?: string
  sellerName?: string
  shippingOptionName?: string
  available?: boolean
  currency?: string
  createdAt?: string
  updatedAt?: string
}
