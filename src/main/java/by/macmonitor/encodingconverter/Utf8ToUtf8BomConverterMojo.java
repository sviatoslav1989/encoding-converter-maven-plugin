/*
 * Copyright 2019 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Created by sviataslau apanasionak on 09.01.2019.
 */

package by.macmonitor.encodingconverter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Mojo(name = "utf8-to-utf8bom")
public class Utf8ToUtf8BomConverterMojo
    extends AbstractMojo
{

    @Parameter(required = true)
    private String filename;

    private byte[] bomMark = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};

    public void execute()
        throws MojoExecutionException
    {

        getLog().info("Utf8 to utf8bom convert: " +filename);
        Path path = Paths.get(filename);

        if(!Files.exists(path)){
            getLog().error("File doesn't exists: " + filename);
            throw new MojoExecutionException("File doesn't exists:" + filename);
        }


        if(!Files.isRegularFile(path)){
            getLog().error("File is not regular file: " + filename);
            throw new MojoExecutionException("File is not regular file: " + filename);
        }


        try(RandomAccessFile file = new RandomAccessFile(filename, "rw")){

            long length = file.length();

            byte content[];

            getLog().info("Try to add BOM to file: " + filename);

            if(length > 0){

                content = new byte[(int)length];
                file.seek(0);
                file.read(content);
                file.seek(0);
                file.write(bomMark);
                file.write(content);

            }else {
                getLog().warn("File length is 0: " + filename);
                file.seek(0);
                file.write(bomMark);
            }

        } catch (FileNotFoundException e) {
            getLog().error("File doesn't exists: " + filename);
            throw new MojoExecutionException("File doesn't exists:" + filename, e);
        } catch (IOException e) {
            getLog().error("Error : " + filename);
            throw new MojoExecutionException("Error:" + filename, e);
        }

        getLog().info("Successful add BOM to file: " + filename);
    }
}
