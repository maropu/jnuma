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

import org.scalatest.{Tag, WordSpec}
import xerial.jnuma.buffer.NumaByteBuffer

class NumaByteBufferTest extends WordSpec {

  implicit def toTag(s:String) = Tag(s)

  "NumaByteBuffer" should {

    "simple test" in {
      val buf = new NumaByteBuffer(8 * 1024 * 1024, 0)
      buf.putLong(0, 3L)
      buf.putLong(8, 39L)
      assert(buf.getLong(0) === 3L)
      assert(buf.getLong(8) === 39L)
    }

    "toDirectByteBuffer" in {
      val buf = new NumaByteBuffer(8 * 1024 * 1024)
      buf.putLong(16, 31L)
      val bb = buf.toDirectByteBuffer(16, 8)
      assert(bb.getLong() === 31L)
    }
  }
}
