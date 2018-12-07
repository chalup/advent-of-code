package org.chalup.utils

operator fun <T, U> Iterable<T>.times(other: Iterable<U>) = flatMap { t -> other.map { u -> t to u } }