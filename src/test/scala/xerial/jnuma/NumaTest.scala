/*
 * Copyright 2012 Taro L. Saito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xerial.jnuma

import java.nio.ByteBuffer
import java.util.logging.Logger

import scala.util.Random
import org.scalatest.{Tag, WordSpec}

import xerial.core.util.Timer

trait MySpec extends WordSpec with Timer {
  implicit def toTag(s:String) = Tag(s)
}

/**
 * @author leo
 */
class NumaTest extends MySpec {
  import NumaTest._

  "Numa" should {
    "report NUMA info" taggedAs "report" in {
      val numNodes = Numa.numNodes()
      val numCpus = Runtime.getRuntime.availableProcessors()
      logger.info(s"numa is available: ${Numa.isAvailable}")
      logger.info(s"num nodes: ${numNodes}")
      logger.info(s"num CPUs: ${numCpus}")
      for (i <- 0 until numNodes) {
        logger.info(s"node ${i} - size:${Numa.nodeSize(i)} free:${Numa.freeSize(i)}")
      }

      for (n1 <- 0 until numNodes; n2 <- n1 until numNodes) {
        logger.info(s"distance ${n1} - ${n2}: ${Numa.distance(n1, n2)}")
      }

      val toBitString: (Array[Long]) => String = (b: Array[Long]) => {
        val s = for (i <- 0 until numCpus) yield {
          if ((b(i / 64) & (1L << (i % 64))) == 0) "0" else "1"
        }
        s.mkString
      }

      val preferred = (0 until numCpus).map { cpu =>
          Numa.runOnNode(cpu % numNodes)
          Numa.setPreferred(cpu % numNodes)
          val n = Numa.getPreferredNode
          Numa.runOnAllNodes()
          n
      }
      logger.info(s"setting prefererd NUMA nodes: ${preferred.mkString(", ")}")
    }

    "allocate local buffer" in {
      for (i <- 0 until 3) {
        val local = Numa.allocLocal(1024)
        Numa.free(local)
      }
    }

    "allocate buffer on nodes" in {
      val N = 100000

      val access: ByteBuffer => Unit = (b: ByteBuffer) => {
        val r = new Random(0)
        var i = 0
        val p = 1024
        val buf = new Array[Byte](p)
        while (i < N) {
          b.position(r.nextInt(b.capacity() / p) * p)
          b.get(buf)
          i += 1
        }
      }

      val bl = ByteBuffer.allocateDirect(8 * 1024 * 1024)
      val bj = ByteBuffer.allocate(8 * 1024 * 1024)
      val b0 = Numa.allocOnNode(8 * 1024 * 1024, 0)
      val b1 = Numa.allocOnNode(8 * 1024 * 1024, 1)
      val bi = Numa.allocInterleaved(8 * 1024 * 1024)

      time("numa random access", repeat = 10) {
        block("direct") { access(bl) }
        block("heap") { access(bj) }
        block("numa0") { access(b0) }
        block("numa1") { access(b1) }
        block("interleaved") { access(bi) }
      }

      Numa.free(b0)
      Numa.free(b1)
      Numa.free(bi)
    }

    "retrieve array from another node" taggedAs "jarray" in {
      def write(f: => Array[Int]) = {
        val r = new Random(13)
        for(i <- 0 until 3) {
          val arr = f
          for (index <- 0 until arr.length) {
            arr(index) = r.nextInt()
          }
        }
      }

      time("numa", repeat = 2) {
        block("node0") {
          Numa.setPreferred(0)
          write {
            val bufSize = 1024 * 1024
            val a = new Array[Int](bufSize)
            Numa.toNodeMemory(a, bufSize * 4, 0)
            a
          }
          Numa.setLocalAlloc
        }

        block("node1") {
          Numa.setPreferred(1)
          write {
            val bufSize = 1024 * 1024
            val a = new Array[Int](bufSize)
            Numa.toNodeMemory(a, bufSize * 4, 1)
            a
          }
          Numa.setLocalAlloc
        }
      }

      Numa.runOnAllNodes()
    }

    "allocate memory" in {
      val size = 1L * 1024 * 1024
      var addr = 0L
      try {
        addr = Numa.allocMemory(size)
      } finally {
        if(addr != 0L)
          Numa.free(addr, size)
      }
    }
  }
}

object NumaTest {
  val logger: Logger = Logger.getLogger(this.getClass.getName())
}
