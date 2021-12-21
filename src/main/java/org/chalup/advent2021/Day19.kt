package org.chalup.advent2021

import org.chalup.utils.Matrix4
import org.chalup.utils.Vector3
import org.chalup.utils.rotations3d
import java.util.LinkedList

private const val MIN_SHARED_BEACONS = 12

object Day19 {
    fun task1(input: List<String>): Int = input
        .let(this::parseScannersData)
        .let { scannersData ->
            val perspectives = buildList {
                scannersData.forEachIndexed { scannerIndex, (beacons) ->
                    beacons.forEachIndexed { beaconIndex, beacon ->
                        add(
                            BeaconPerspective(
                                scannerIndex,
                                beaconIndex,
                                beacons.mapNotNull { otherBeacon ->
                                    otherBeacon
                                        .takeUnless { it === beacon }
                                        ?.let { beacon - it }
                                }
                            )
                        )
                    }
                }
            }

            val potentiallyMatchingPerspectives = perspectives
                .asSequence()
                .flatMap { perspective ->
                    perspectives
                        .asSequence()
                        .filter { it.scannerIndex > perspective.scannerIndex }
                        .map { perspective to it }
                }
                .filter { (one, other) -> (one.otherBeaconsSquaredDistances intersect other.otherBeaconsSquaredDistances).size >= MIN_SHARED_BEACONS - 1 }
                .distinctBy { (one, other) -> one.scannerIndex to other.scannerIndex }
                .toList()

            val connections = potentiallyMatchingPerspectives
                .asSequence()
                .flatMap { (one, other) ->
                    sequenceOf(
                        one to other,
                        other to one,
                    )
                }
                .flatMap { (one, other) ->
                    rotations3d
                        .mapNotNull { rotation ->
                            val setA = one.otherBeacons
                            val setB = other.otherBeacons.map { (it.toVector4() * rotation).toVector3() }

                            val intersection = setA intersect setB

                            if (intersection.size >= MIN_SHARED_BEACONS - 1) {
                                val translateDestination = Matrix4.translation(-scannersData[other.scannerIndex].beaconVectors[other.beaconIndex])
                                val translateSource = Matrix4.translation(scannersData[one.scannerIndex].beaconVectors[one.beaconIndex])

                                ScannersConnection(
                                    sourceIndex = one.scannerIndex,
                                    destinationIndex = other.scannerIndex,
                                    transform = translateDestination * rotation * translateSource
                                )
                            } else {
                                null
                            }
                        }
                }
                .toSet()

            val routes = connections
                .groupBy { it.sourceIndex }
                .mapValues { (_, connections) ->
                    connections.map { (_, destinationIndex, transform) -> Route(destinationIndex, transform) }
                }

            val scannersToVisit = scannersData.indices.toMutableSet()
            val beacons = mutableSetOf<Vector3>()
            val enqueuedRoutes = LinkedList<Route>().apply { add(Route(0, Matrix4.IDENTITY)) }
            while (enqueuedRoutes.isNotEmpty() && scannersToVisit.isNotEmpty()) {
                val route = enqueuedRoutes.poll()

                if (route.destinationIndex !in scannersToVisit) continue

                scannersToVisit.remove(route.destinationIndex)
                scannersData[route.destinationIndex].beaconVectors.mapTo(beacons) { (it.toVector4() * route.transform).toVector3() }

                routes[route.destinationIndex]
                    .orEmpty()
                    .filter { it.destinationIndex in scannersToVisit }
                    .mapTo(enqueuedRoutes) { it.copy(transform = it.transform * route.transform) }
            }

            beacons
        }
        .size

    private fun parseScannersData(input: List<String>) = buildList {
        val iterator = input.iterator()

        while (iterator.hasNext()) {
            iterator.next().also { check(it.startsWith("---")) }

            val beaconVectors = buildList {
                while (iterator.hasNext()) {
                    val line = iterator.next()

                    if (line.isBlank()) break

                    line.split(',')
                        .map(String::toLong)
                        .also { (x, y, z) -> add(Vector3(x, y, z)) }
                }
            }

            add(ScannerData(beaconVectors))
        }
    }

    data class ScannerData(val beaconVectors: List<Vector3>)

    data class ScannersConnection(
        val sourceIndex: Int,
        val destinationIndex: Int,
        val transform: Matrix4,
    )

    data class Route(
        val destinationIndex: Int,
        val transform: Matrix4,
    )

    data class BeaconPerspective(
        val scannerIndex: Int,
        val beaconIndex: Int,
        val otherBeacons: List<Vector3>,
    ) {
        val otherBeaconsSquaredDistances: List<Long> = otherBeacons.map {
            it.x * it.x + it.y * it.y + it.z * it.z
        }
    }
}
